package net.backslot.client.profile;

public record TransformProfile(
        double offsetX,
        double offsetY,
        double offsetZ,
        double rotationX,
        double rotationY,
        double rotationZ,
        double scale
) {
}
