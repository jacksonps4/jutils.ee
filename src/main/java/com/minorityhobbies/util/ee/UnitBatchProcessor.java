package com.minorityhobbies.util.ee;

import javax.batch.api.chunk.ItemProcessor;

public class UnitBatchProcessor implements ItemProcessor {
	@Override
	public final Object processItem(Object item) throws Exception {
		return item;
	}
}
