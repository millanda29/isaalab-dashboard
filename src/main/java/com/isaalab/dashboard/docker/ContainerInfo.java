package com.isaalab.dashboard.docker;

public record ContainerInfo(
        String containerId,
        String name,
        String image,
        String status,
        String ports
) {}
