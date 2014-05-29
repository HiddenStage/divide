package io.divide.client;

/**
 * Created by williamwebb on 4/4/14.
 */
public final class BackendConfig extends Config<Backend>{

    @Override
    public Class<Backend> getType() {
        return Backend.class;
    }

    public BackendConfig(String fileSavePath, String url){
        this(fileSavePath, url, BackendModule.class);
    }

    public <ModuleType extends BackendModule<Backend>> BackendConfig(String fileSavePath, String url, Class<ModuleType> moduleClass){
        super(fileSavePath,url,moduleClass);
    };
}
