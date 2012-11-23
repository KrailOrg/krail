package uk.co.q3c.basic;

import com.solstoneplus.campus.base.app.CampusApplication;

/**
 * This class is entirely passive - it is a surrogate for MainWindow during the IoC process in support of
 * {@link MainWindowScoped}. It is needed because MainWindow is needed as a key in {@link MainWindowScope}, but because
 * it uses constructor injection itself,is needed before it exists! This class acts a simple surrogate key
 * 
 * @see CampusApplication#createNewNavigableAppLevelWindow()
 */
public class MainWindowKey {

}
