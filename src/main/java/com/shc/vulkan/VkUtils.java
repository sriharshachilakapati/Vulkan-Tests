package com.shc.vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

/**
 * @author Sri Harsha Chilakapati
 */
public final class VkUtils
{
    private VkUtils()
    {
    }

    /**
     * Utility method to create a Vulkan instance. It is used by all the examples except the InstanceExample to reduce
     * the duplication of code. For explanation and comments, please see the InstanceExample class.
     *
     * @param applicationName The Application name to be used when creating the instance.
     * @param extensions      The list of extensions to enable for this instance.
     *
     * @return The creating VkInstance used to deal with the Vulkan API.
     */
    public static VkInstance createInstance(String applicationName, String... extensions)
    {
        VkInstance instance;

        PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();

        PointerBuffer enabledExtensionNames = memAllocPointer(extensions.length + glfwExtensions.remaining());
        ByteBuffer[] extensionNames = new ByteBuffer[extensions.length];

        for (int i = 0; i < extensions.length; i++)
        {
            String extensionName = extensions[i];
            extensionNames[i] = memASCII(extensionName);

            enabledExtensionNames.put(extensionNames[i]);
        }

        while (glfwExtensions.remaining() > 0)
            enabledExtensionNames.put(glfwExtensions.get());

        enabledExtensionNames.flip();

        VkApplicationInfo appInfo = VkApplicationInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                .pApplicationName(memASCII(applicationName))
                .pEngineName(memASCII(""))
                .apiVersion(VK_MAKE_VERSION(1, 0, 4));

        VkInstanceCreateInfo instInfo = VkInstanceCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                .pNext(NULL)
                .pApplicationInfo(appInfo)
                .ppEnabledExtensionNames(enabledExtensionNames);

        PointerBuffer pInstance = memAllocPointer(1);

        vkCreateInstance(instInfo, null, pInstance);

        instance = new VkInstance(pInstance.get(), instInfo);

        memFree(pInstance);

        appInfo.free();
        instInfo.free();

        memFree(enabledExtensionNames);

        for (ByteBuffer byteBuffer : extensionNames)
            memFree(byteBuffer);

        return instance;
    }

    public static String translatePhysicalDeviceType(int deviceType)
    {
        switch (deviceType)
        {
            case VK_PHYSICAL_DEVICE_TYPE_CPU:
                return "CPU";

            case VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU:
                return "Discrete GPU";

            case VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU:
                return "Integrated GPU";

            case VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU:
                return "Virtual GPU";

            case VK_PHYSICAL_DEVICE_TYPE_OTHER:
                return "Other";
        }

        return null;
    }

    /**
     * Returns the handle to the first physical device connected to the system.
     *
     * @param instance The instance of the vulkan
     *
     * @return The handle to the first physical device.
     */
    public static VkPhysicalDevice getFirstPhysicalDevice(VkInstance instance)
    {
        IntBuffer gpuCount = memAllocInt(1);

        vkEnumeratePhysicalDevices(instance, gpuCount, null);
        PointerBuffer devices = memAllocPointer(gpuCount.get(0));
        vkEnumeratePhysicalDevices(instance, gpuCount, devices);

        VkPhysicalDevice firstPhysicalDevice = new VkPhysicalDevice(devices.get(0), instance);

        memFree(gpuCount);
        memFree(devices);

        return firstPhysicalDevice;
    }
}
