package my.fun.asyncload.imageloader.model;

import java.util.Locale;

/**
 * Created by admin on 2016/10/9.
 */
public enum  Scheme {
    HTTP("http"), HTTPS("https"), FILE("file"), CONTENT("content"), ASSETS("assets"), DRAWABLE("drawable"), UNKNOWN("");

    private String scheme;
    private String uriPrefix;

    Scheme(String scheme){
        this.scheme = scheme;
        uriPrefix = scheme + "://";
    }

    public static Scheme ofScheme(String uri){
        if (uri != null){
            for (Scheme s : values()){
                if (s.belongs(uri)){
                    return s;
                }
            }
        }
        return UNKNOWN;
    }

    private boolean belongs(String uri) {
        return uri.toLowerCase(Locale.US).startsWith(uriPrefix);
    }

    public String wrap(String path){
        return uriPrefix+path;
    }

    public String crop(String uri){
        if (!belongs(uri)){
            throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, scheme));
        }
        return uri.substring(uriPrefix.length());
    }
}
