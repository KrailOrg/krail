/*
 * Copyright (C) 2014 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.co.q3c.v7.testutil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import com.google.inject.Inject;

public class LogMonitor {

	class MemoryAppender implements Appender {

		Map<Level, List<String>> logs;

		protected MemoryAppender() {
			super();
			logs = new HashMap<>();
			logs.put(Level.TRACE, new LinkedList<String>());
			logs.put(Level.DEBUG, new LinkedList<String>());
			logs.put(Level.INFO, new LinkedList<String>());
			logs.put(Level.WARN, new LinkedList<String>());
			logs.put(Level.ERROR, new LinkedList<String>());
		}

		@Override
		public void addFilter(Filter newFilter) {
		}

		@Override
		public Filter getFilter() {
			return null;
		}

		@Override
		public void clearFilters() {
		}

		@Override
		public void close() {
			logs.clear();
		}

		@Override
		public void doAppend(LoggingEvent event) {
			List<String> list = logs.get(event.getLevel());
			list.add(event.getRenderedMessage());
		}

		@Override
		public String getName() {
			return "In memory appender";
		}

		@Override
		public void setErrorHandler(ErrorHandler errorHandler) {

		}

		@Override
		public ErrorHandler getErrorHandler() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setLayout(Layout layout) {

		}

		@Override
		public Layout getLayout() {
			return null;
		}

		@Override
		public void setName(String name) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean requiresLayout() {
			return false;
		}

	}

	private static Logger log = Logger.getRootLogger();
	private final MemoryAppender appender;

	@Inject
	public LogMonitor() {
		appender = new MemoryAppender();
		log.addAppender(appender);
	}

	public List<String> infoLogs() {
		return appender.logs.get(Level.INFO);
	}

	public List<String> warnLogs() {
		return appender.logs.get(Level.WARN);
	}

	public List<String> errorLogs() {
		return appender.logs.get(Level.ERROR);
	}

	public List<String> debugLogs() {
		return appender.logs.get(Level.DEBUG);
	}

	public List<String> traceLogs() {
		return appender.logs.get(Level.TRACE);
	}

	public void close() {
		log.removeAppender(appender);
	}

}
