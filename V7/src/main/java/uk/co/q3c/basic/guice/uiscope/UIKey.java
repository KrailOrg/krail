package uk.co.q3c.basic.guice.uiscope;


/**
 * This class is entirely passive - it is a surrogate for MainWindow during the IoC process in support of
 * {@link UIScoped}. It is needed because MainWindow is needed as a key in {@link UIScope}, but because
 * it uses constructor injection itself, is needed before it exists! This class acts a simple surrogate key
 * 
 */
public class UIKey {

}
