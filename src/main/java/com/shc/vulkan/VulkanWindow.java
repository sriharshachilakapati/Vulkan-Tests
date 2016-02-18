package com.shc.vulkan;

import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.VK10.*;

/**
 * @author Sri Harsha Chilakapati
 */
public class VulkanWindow
{
    public static void main(String[] args)
    {
        if (glfwInit() != GLFW_TRUE)
            System.exit(-1);

        if (glfwVulkanSupported() != GLFW_TRUE)
            System.exit(-2);

        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        long windowID = glfwCreateWindow(800, 600, "Hello Vulkan", NULL, NULL);

        VkApplicationInfo appInfo = VkApplicationInfo.malloc()
                .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                .pApplicationName("VulkanTests")
                .pEngineName("VulkanTests")
                .apiVersion(VK_API_VERSION)
                .engineVersion(1)
                .applicationVersion(1)
                .pNext(NULL);

        VkInstanceCreateInfo instInfo = VkInstanceCreateInfo.malloc()
                .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                .pNext(NULL)
                .flags(0)
                .pApplicationInfo(appInfo)
                .enabledExtensionCount(0)
                .ppEnabledExtensionNames(null)
                .enabledLayerCount(0)
                .ppEnabledLayerNames(null);

        ByteBuffer byteBuffer = memAlloc(Long.BYTES);
        ByteBuffer byteBuffer2 = memAlloc(Long.BYTES);

        int res = vkCreateInstance(instInfo, null, byteBuffer);

        long instance = byteBuffer.getLong(0);

        if (res == VK_ERROR_INCOMPATIBLE_DRIVER)
            System.exit(-3);

        if (res != 0)
            System.exit(-4);

        int error = glfwCreateWindowSurface(instance, windowID, null, byteBuffer2);

        if (error != 0)
            System.exit(-5);

        long surface = byteBuffer2.getLong(0);

        while (glfwWindowShouldClose(windowID) != GLFW_TRUE)
        {
            glfwPollEvents();
        }

        vkDestroySurfaceKHR(instance, surface, null);
        vkDestroyInstance(instance, null);

        instInfo.free();
        appInfo.free();

        memFree(byteBuffer);
        memFree(byteBuffer2);

        glfwTerminate();
    }
}
