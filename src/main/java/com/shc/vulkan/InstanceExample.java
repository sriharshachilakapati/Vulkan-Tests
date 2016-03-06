package com.shc.vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VKCapabilities;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.VK10.*;

/**
 * This example demonstrates the creation of a Vulkan instance. It shows what are the VkApplicationInfo and the
 * VkInstanceCreateInfo structs, how to create them, when to free them, and also how to request the extensions.
 *
 * @author Sri Harsha Chilakapati
 */
public class InstanceExample extends VulkanExample
{
    private VkInstance instance;

    public static void main(String[] args)
    {
        title = "Vulkan Instance Example";
        new InstanceExample().start();
    }

    @Override
    public VkInstance initVulkan()
    {
        // We need to say which extensions to enable at the time of creating the instance. We should also add in the
        // extensions required by GLFW to create the surface. Note that this should not be freed manually.
        PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();

        // Create a PointerBuffer with memory enough to hold pointers for all the extension names.
        PointerBuffer enabledExtensionNames = memAllocPointer(glfwExtensions.remaining() + 1);

        // Encode the surface extension names into a ByteBuffer so we can put it in the PointerBuffer.
        // Also note that it is a must to use MALLOC as BufferAllocator and NIO will not work.
        ByteBuffer KHR_SURFACE_EXTENSION = memEncodeASCII(VK_KHR_SURFACE_EXTENSION_NAME, BufferAllocator.MALLOC);

        // Add the extensions to the PointerBuffer and flip the buffer. In order to present something
        // we must request the KHR_SURFACE_EXTENSION, without which, the instance will act like an offscreen context.
        enabledExtensionNames.put(KHR_SURFACE_EXTENSION);

        // Also put in the GLFW extensions into the enabledExtensionNames list so they get enabled too.
        while (glfwExtensions.remaining() > 0)
            enabledExtensionNames.put(glfwExtensions.get());

        // Flip the buffer so that the system can read from the buffer.
        enabledExtensionNames.flip();

        // The VkApplicationInfo struct contains information about the application that we are going to create.
        VkApplicationInfo appInfo = VkApplicationInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                .pApplicationName("Vulkan Instance Example")
                .pEngineName("")
                .apiVersion(VK_MAKE_VERSION(1, 0, 4));

        // The VkInstanceCreateInfo struct contains information about the Vulkan instance, and refers to the appInfo.
        VkInstanceCreateInfo instInfo = VkInstanceCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                .pNext(NULL)
                .pApplicationInfo(appInfo)
                .ppEnabledExtensionNames(enabledExtensionNames);

        // The PointerBuffer enough to hold one pointer, the PointerBuffer is not a pointer, but it's contents are.
        PointerBuffer pInstance = memAllocPointer(1);

        // Create the instance. The instance handle is stored in the PointerBuffer which we can use now.
        vkCreateInstance(instInfo, null, pInstance);

        // Get the VkInstance handle from the pointer
        instance = new VkInstance(pInstance.get(), instInfo);

        // Free the pointer buffer, not the VkInstance struct
        memFree(pInstance);

        // Free the VkApplicationInfo and VkInstanceCreateInfo structs, we no longer need them in our application.
        appInfo.free();
        instInfo.free();

        // Free the extension names, we don't need them now.
        memFree(enabledExtensionNames);
        memFree(KHR_SURFACE_EXTENSION);

        // Print out the instance capabilities
        VKCapabilities capabilities = instance.getCapabilities();

        System.out.println("Vulkan10: " + capabilities.Vulkan10);
        System.out.println("VK_KHR_display: " + capabilities.VK_KHR_display);
        System.out.println("VK_KHR_surface: " + capabilities.VK_KHR_surface);
        System.out.println("VK_KHR_swapchain: " + capabilities.VK_KHR_swapchain);
        System.out.println("VK_EXT_debug_report: " + capabilities.VK_EXT_debug_report);
        System.out.println("VK_KHR_xlib_surface: " + capabilities.VK_KHR_xlib_surface);
        System.out.println("VK_KHR_win32_surface: " + capabilities.VK_KHR_win32_surface);
        System.out.println("VK_KHR_display_swapchain: " + capabilities.VK_KHR_display_swapchain);
        System.out.println("VK_KHR_sampler_mirror_clamp_to_edge: " + capabilities.VK_KHR_sampler_mirror_clamp_to_edge);

        // Return instance to attach to the display so rendering can be done.
        return instance;
    }

    @Override
    public void render()
    {
        // No rendering in this example, this is just to explain how to create the instance.
    }

    @Override
    public void cleanUp()
    {
        // Destroy the vulkan instance.
        vkDestroyInstance(instance, null);
    }
}
