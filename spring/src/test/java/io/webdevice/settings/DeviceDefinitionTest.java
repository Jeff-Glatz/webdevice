package io.webdevice.settings;

public interface DeviceDefinitionTest {

    // No specified capabilities
    void shouldBuildDefinitionWithoutCapabilitiesAndWithoutConfidential();
    void shouldBuildDefinitionWithoutCapabilitiesAndWithConfidential();

    // Capabilities originating from bean in context
    void shouldBuildDefinitionWithCapabilitiesReference();

    // Capabilities originating from options
    void shouldBuildDefinitionWithOptionsOnly();
    void shouldBuildDefinitionWithOptionsMergingCapabilities();
    void shouldBuildDefinitionWithOptionsMergingExtraCapabilities();
    void shouldBuildDefinitionWithOptionsMergingCapabilitiesAndExtraCapabilities();

    // Capabilities originating from DesiredCapabilities.xxx()
    void shouldBuildDefinitionWithDesiredOnly();
    void shouldBuildDefinitionWithDesiredMergingCapabilities();
    void shouldBuildDefinitionWithDesiredMergingExtraCapabilities();
    void shouldBuildDefinitionWithDesiredMergingCapabilitiesAndExtraCapabilities();

    // Capabilities originating from Map
    void shouldBuildDefinitionWithMapOnly();
    void shouldBuildDefinitionWithMapMergingExtraCapabilities();
}
