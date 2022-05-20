package org.flower.risibank.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public class RisibankException extends Throwable{

    @NotNull Exception cause;
    @Nullable URL urlRequest;

    public RisibankException(@NotNull Exception cause, @Nullable URL url){
        this.cause = cause;
        this.urlRequest = url;
    }

    public @NotNull Exception getCause() {
        return cause;
    }

    public URL getUrlRequest() {
        return urlRequest;
    }
}
