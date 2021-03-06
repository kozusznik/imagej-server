/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2014 - 2016 Board of Regents of the University of
 * Wisconsin-Madison, University of Konstanz and Brian Northan.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.server.services;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service that handles concurrent Object registration and retrieval using IDs.
 * 
 * @author Leon Yang
 */
public class DefaultObjectService implements ObjectService {

	final private ConcurrentHashMap<String, ObjectInfo> id2obj;
	final private ConcurrentHashMap<Object, String> obj2id;

	public DefaultObjectService() {
		id2obj = new ConcurrentHashMap<>();
		obj2id = new ConcurrentHashMap<>();
	}

	@Override
	public Set<String> getIds() {
		return Collections.unmodifiableSet(id2obj.keySet());
	}

	@Override
	public String register(final Object object, final String createdBy) {
		final DefaultObjectInfo info = new DefaultObjectInfo(object, createdBy);
		final String prev = obj2id.putIfAbsent(object, info.getId());

		if (prev != null) return prev;

		id2obj.put(info.getId(), info);
		return info.getId();
	}

	@Override
	public boolean remove(final String id) {
		if (!id2obj.containsKey(id)) return false;
		final ObjectInfo info = id2obj.get(id);
		return id2obj.remove(id, info) && obj2id.remove(info.getObject(), id);
	}

	@Override
	public ObjectInfo find(final String id) {
		return id2obj.get(id);
	}

	@Override
	public boolean contains(final String id) {
		return id2obj.containsKey(id);
	}
}
