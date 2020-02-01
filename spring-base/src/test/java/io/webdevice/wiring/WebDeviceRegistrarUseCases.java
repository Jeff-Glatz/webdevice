package io.webdevice.wiring;

import org.junit.Test;

public interface WebDeviceRegistrarUseCases {

    @Test
    void shouldLoadFromAllDevices()
            throws Exception;

    @Test
    void shouldUseCustomBinderToBindSettingsFromEnvironment()
            throws Exception;

    @Test
    void shouldRegisterSettings()
            throws Exception;

    @Test
    void shouldSkipRegisteringDeviceIfAlreadyDefined()
            throws Exception;

    @Test
    void shouldSkipRegisteringPoolForPooledDeviceIfAlreadyDefinedAndAliasPoolWithDeviceName()
            throws Exception;

    @Test
    void shouldSkipRegisteringProviderForPooledDeviceIfAlreadyDefinedAndAliasPoolWithDeviceName()
            throws Exception;

    @Test
    void shouldRegisterPooledDeviceAndAliasPoolWithDeviceName()
            throws Exception;

    @Test
    void shouldRegisterUnPooledDeviceAndAliasProviderWithDeviceName()
            throws Exception;

    @Test
    void shouldRegisterWebDeviceAndDeviceRegistryInConfiguredScope()
            throws Exception;

    @Test
    void shouldRegisterWebDeviceAndDeviceRegistryInDefaultScope()
            throws Exception;

    @Test
    void shouldRegisterWebDeviceAndDeviceRegistryInCucumberScope();
}
