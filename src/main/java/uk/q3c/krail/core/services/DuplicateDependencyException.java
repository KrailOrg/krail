package uk.q3c.krail.core.services;

/**
 * Created by David Sowerby on 17 Dec 2015
 */
public class DuplicateDependencyException extends RuntimeException {
    public DuplicateDependencyException(String msg) {
        super(msg);
    }
}
