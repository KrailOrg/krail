package uk.co.q3c.v7.base.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.config.ApplicationConfiguration;
import uk.co.q3c.v7.base.config.ConfigKeys;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Broadcaster {
	private static Logger log = LoggerFactory.getLogger(Broadcaster.class);
	public static final String ALL_MESSAGES = "all";

	private final ExecutorService executorService;
	private final Map<String, List<BroadcastListener>> groups = new HashMap<>();
	private final List<BroadcastListener> allGroup = new ArrayList<>();
	private final ApplicationConfiguration applicationConfiguration;

	public interface BroadcastListener {
		void receiveBroadcast(String group, String message);
	}

	@Inject
	protected Broadcaster(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
		executorService = Executors.newSingleThreadExecutor();
	}

	/**
	 * Register a listener to receive messages for {@code group}. If you want the listener to receive all messages call
	 * with {@code group}= {@link PushMessageRouter#ALL_MESSAGES}. If you want to register for more than one group, make
	 * multiple calls.
	 * 
	 * @param group
	 * @param listener
	 */
	public synchronized void register(String group, BroadcastListener listener) {
		log.debug("adding listener: {}", listener.getClass().getName());
		if (group == ALL_MESSAGES) {
			allGroup.add(listener);
		} else {
			List<BroadcastListener> listenerGroup = groups.get(group);
			if (listenerGroup == null) {
				listenerGroup = new ArrayList<>();
				groups.put(group, listenerGroup);
			}
			listenerGroup.add(listener);
		}

	}

	/**
	 * Unregister a listener to receive messages for {@code group}.
	 */
	public synchronized void unregister(String group, BroadcastListener listener) {
		if (group == ALL_MESSAGES) {
			allGroup.remove(listener);
		} else {
			List<BroadcastListener> listenerGroup = groups.get(group);
			if (listenerGroup != null) {
				listenerGroup.remove(listener);
			}
		}
	}

	/**
	 * Send a message to registered listeners
	 * 
	 * @param group
	 * @param message
	 */
	public synchronized void broadcast(final String group, final String message) {
		if (applicationConfiguration.getBoolean(ConfigKeys.NOTIFICATION_PUSH_ENABLED, true)) {
			log.debug("broadcasting message: {}", message);
			List<BroadcastListener> listenerGroup = groups.get(group);
			if (listenerGroup != null) {
				for (final BroadcastListener listener : listenerGroup)
					executorService.execute(new Runnable() {
						@Override
						public void run() {
							listener.receiveBroadcast(group, message);
						}
					});
			}
			for (final BroadcastListener listener : allGroup)
				executorService.execute(new Runnable() {
					@Override
					public void run() {
						listener.receiveBroadcast(group, message);
					}
				});
		} else {
			log.debug("server push is disabled, message not broadcast");
		}
	}

}
