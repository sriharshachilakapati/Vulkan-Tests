package com.shc.vulkan;

import org.lwjgl.vulkan.VkInstance;

import java.nio.LongBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * This is the base class that all the Vulkan examples in this repository extends from. This class takes care of the
 * window creation using GLFW and also the event loop for the window. It also provides a utility method (macro in C)
 * called as {@code VK_MAKE_VERSION} which allows to create the version.
 *
 * @author Sri Harsha Chilakapati
 */
public abstract class VulkanExample
{
    /**
     * The ID of the window that the example uses to display the Vulkan output to the user.
     */
    protected long windowID;

    /**
     * The title string of the window. Can be changed to change the title of the window before starting
     * the event loop.
     */
    protected static String title;

    /**
     * This is a utility method that does the job of the VK_MAKE_VERSION macro in the C sources. This is used to pack
     * the Vulkan version that these demos require into a single int which we can pass to VkApplicationInfo struct.
     *
     * @param major The major Vulkan version.
     * @param minor The minor Vulkan version.
     * @param patch The patch Vulkan version.
     *
     * @return A single int that represents the packed Vulkan version number.
     */
    public static int VK_MAKE_VERSION(int major, int minor, int patch)
    {
        return (major << 22) | (minor << 12) | patch;
    }

    /**
     * This is a utility method that does the opposite of the VK_MAKE_VERSION method, that it extracts the version from
     * the packed integer and returns it as major minor patch values in an int array.
     *
     * @param version The packed version integer that contains vulkan version.
     *
     * @return An array containing major, minor and patch versions in the order.
     */
    public static int[] VK_EXTRACT_VERSION(int version)
    {
        int[] versions = new int[3];

        versions[0] = version >> 22;
        versions[1] = version >> 12 & 0xF;
        versions[2] = version & 0xF;

        return versions;
    }

    /**
     * The job of this method is to initialize Vulkan and return the VkInstance struct handle so that it is used to
     * create a surface and attaches with the window. The initialization code should request the KHR_SURFACE extension
     * to be able to create the surface.
     *
     * @return The VkInstance handle to the successfully initialized Vulkan instance.
     */
    public abstract VkInstance initVulkan();

    /**
     * This method is used to present anything onto the screen. It will be called throughout the life of the example
     * application.
     */
    public abstract void render();

    /**
     * This method is called before closing the example, and is required to do any cleanup of resources like the vulkan
     * instance, any buffers that are in use, shaders etc.,
     */
    public abstract void cleanUp();

    /**
     * The main heart of an example, it runs the event loop and manages the creation and destruction of the window and
     * also creates the surface for Vulkan.
     */
    public void start()
    {
        glfwInit();

        // Check if Vulkan is available, if not, exit early.
        if (glfwVulkanSupported() == GLFW_FALSE)
            throw new UnsupportedOperationException("Vulkan driver is not found on this machine. Please update your drivers.");

        // Create a GLFW window with no API
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);

        if (title == null)
            title = "Vulkan Tests";

        windowID = glfwCreateWindow(800, 600, title, NULL, NULL);

        // Initialize Vulkan and get the instance.
        VkInstance vulkanInstance = initVulkan();

        // Create the surface and attach to the window.
        LongBuffer pSurface = memAllocLong(1);
        int err = glfwCreateWindowSurface(vulkanInstance, windowID, null, pSurface);

        if (err != 0)
            throw new IllegalStateException("Could not create surface for Vulkan");

        long surfaceID = pSurface.get();

        memFree(pSurface);

        while (glfwWindowShouldClose(windowID) == GLFW_FALSE)
        {
            glfwPollEvents();
            render();
        }

        vkDestroySurfaceKHR(vulkanInstance, surfaceID, null);
        cleanUp();

        glfwTerminate();
    }
}
