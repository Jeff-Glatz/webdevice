# Polish
- [ ] Spring Configuration Metadata

# Miscellaneous 
- [ ] When `@DirtiesContext` is used, there is a shutdown order issue where 
singleton-scoped `WebDevicePool` instances are disposed of before the 
glue-code-scoped `WebDevice`. `WebDevice` needs to de disposed of first.