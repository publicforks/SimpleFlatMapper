package org.sfm.map;

import org.junit.Test;
import org.sfm.map.error.RethrowRowHandlerErrorHandler;

import static org.junit.Assert.assertSame;

public class RethrowRowHandlerErrorHandlerTest {

	@Test
	public void testHandlerError() {
		RethrowRowHandlerErrorHandler handler = new RethrowRowHandlerErrorHandler();
		
		Exception error = new Exception();
		try {
			handler.handlerError(error, this);
		} catch(Exception e) {
			assertSame(error, e);
		}
	}

}
