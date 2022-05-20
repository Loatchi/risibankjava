package org.flower.risibank.exceptions;

import org.jetbrains.annotations.NotNull;

public class UserDoesNotExistException extends Exception{
    @NotNull String username;
    public UserDoesNotExistException(@NotNull String username){
        this.username = username;
    }
}
