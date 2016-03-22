package com.shc.vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceLimits;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

import java.nio.IntBuffer;
import java.util.UUID;

import static com.shc.vulkan.VkUtils.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.VK10.*;

/**
 * This example demonstrates enumerating the Physical devices from a Vulkan instance. This explains how to get a list of
 * the physical devices, and how to find the properties of the devices.
 *
 * @author Sri Harsha Chilakapati
 */
public class PhysicalDevicesExample extends VulkanExample
{
    private VkInstance instance;

    public static void main(String[] args)
    {
        title = "PhysicalDevices Example";
        new PhysicalDevicesExample().start();
    }

    @Override
    public VkInstance initVulkan()
    {
        // See InstanceExample for instructions on how to create the instance.
        String[] extensions = {
                VK_KHR_SURFACE_EXTENSION_NAME
        };

        // Create a Vulkan instance. It is covered extensively with comments in InstanceExample,
        // but this function createInstance(title, extensions) is from the VkUtils class.
        instance = createInstance(title, extensions);

        // Create an int buffer to hold one int. This int tells us the number of GPUs that are present on the system.
        IntBuffer gpuCount = memAllocInt(1);

        // Query the number of physical devices that are connected to the system. We do this by using the function
        // vkEnumeratePhysicalDevices which can be used for two purposes, query the number of GPUs, and also query the
        // list of connected GPUs. We pass null when we just want the GPU count.
        vkEnumeratePhysicalDevices(instance, gpuCount, null);

        // Now create a PointerBuffer to be able to hold the number of pointers (one for each device) specified by
        // the integer in gpuCount that we queried recently.
        PointerBuffer devices = memAllocPointer(gpuCount.get(0));

        // Now we call vkEnumeratePhysicalDevices again, but this time to actually retrieve the device pointers.
        // We tell the system to get us the first gpuCount number of devices.
        vkEnumeratePhysicalDevices(instance, gpuCount, devices);

        // Now we loop over the physical devices, so that we can observe their properties.
        for (int i = 0; i < gpuCount.get(0); i++)
        {
            // The devices buffer does contain pointers for the VkPhysicalDevice type, but we need to use the wrapper
            // LWJGL provides since pointers are in the form of long primitives in Java. We additionally need to pass
            // the instance to the VkPhysicalDevice class constructor so that it can use the instance to get the
            // VkCapabilities instance.
            VkPhysicalDevice physicalDevice = new VkPhysicalDevice(devices.get(i), instance);

            // Next we create an struct instance of VkPhysicalDeviceProperties type. We use this to cache all the
            // properties of a physical device.
            VkPhysicalDeviceProperties physicalDeviceProperties = VkPhysicalDeviceProperties.calloc();

            // This tells the driver to store all the properties of the physicalDevice in the physicalDeviceProperties
            vkGetPhysicalDeviceProperties(physicalDevice, physicalDeviceProperties);

            // Print out some of the device properties.
            System.out.println("Physical Device " + i);
            System.out.println("~~~~~~~~~~~~~~~~~");

            int[] apiVersion = VK_EXTRACT_VERSION(physicalDeviceProperties.apiVersion());

            System.out.println("\tAPI Version: " + String.format("%d.%d.%d", apiVersion[0], apiVersion[1], apiVersion[2]));
            System.out.println("\tDriver Version: " + physicalDeviceProperties.driverVersion());
            System.out.println("\tVendor ID: " + physicalDeviceProperties.vendorID());
            System.out.println("\tDevice ID: " + physicalDeviceProperties.deviceID());
            System.out.println("\tDevice Type: " + translatePhysicalDeviceType(physicalDeviceProperties.deviceType()));
            System.out.println("\tDevice Name: " + physicalDeviceProperties.deviceNameString());

            long uuidLong1 = physicalDeviceProperties.pipelineCacheUUID().getLong(0);
            long uuidLong2 = physicalDeviceProperties.pipelineCacheUUID().getLong(1);

            UUID pipelineCacheUUID = new UUID(uuidLong1, uuidLong2);
            System.out.println("\tPipeline Cache UUID: " + pipelineCacheUUID);

            // Each device does has it's own limits, so use the properties to get it's limits.
            VkPhysicalDeviceLimits physicalDeviceLimits = physicalDeviceProperties.limits();

            System.out.println("\tLimits");
            System.out.println("\t~~~~~~");
            System.out.println("\t\tMax Image Dimension 1D: " + physicalDeviceLimits.maxImageDimension1D());
            System.out.println("\t\tMax Image Dimension 2D: " + physicalDeviceLimits.maxImageDimension2D());
            System.out.println("\t\tMax Image Dimension 3D: " + physicalDeviceLimits.maxImageDimension3D());
            System.out.println("\t\tMax Image Dimension Cube: " + physicalDeviceLimits.maxImageDimensionCube());
            System.out.println("\t\tMax Image Array Layers: " + physicalDeviceLimits.maxImageArrayLayers());
            System.out.println("\t\tMax Texel Buffer Elements: " + physicalDeviceLimits.maxTexelBufferElements());
            System.out.println("\t\tMax Uniform Buffer Range: " + physicalDeviceLimits.maxUniformBufferRange());
            System.out.println("\t\tMax Storage Buffer Range: " + physicalDeviceLimits.maxStorageBufferRange());
            System.out.println("\t\tMax Push Constants Size: " + physicalDeviceLimits.maxPushConstantsSize());
            System.out.println("\t\t");
            System.out.println("\t\tBuffer Image Granularity: " + physicalDeviceLimits.bufferImageGranularity());
            System.out.println("\t\tDiscrete Queue Priorities: " + physicalDeviceLimits.discreteQueuePriorities());
            System.out.println("\t\tFramebuffer Color Sample Counts: " + physicalDeviceLimits.framebufferColorSampleCounts());

            // There are a lot of other limits in the device, but let's ignore them. We will take only what we like.

            // Free the device properties now
            physicalDeviceProperties.free();
        }

        // Free the gpuCount, the integer that we dynamically allocated before. Also free the devices, we do not need
        // them anymore in this example.
        memFree(gpuCount);
        memFree(devices);

        return instance;
    }


    @Override
    public void render()
    {

    }

    @Override
    public void cleanUp()
    {
        vkDestroyInstance(instance, null);
    }
}
