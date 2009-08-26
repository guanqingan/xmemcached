package net.rubyeye.xmemcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import net.rubyeye.xmemcached.buffer.BufferAllocator;
import net.rubyeye.xmemcached.buffer.SimpleBufferAllocator;
import net.rubyeye.xmemcached.command.TextCommandFactory;
import net.rubyeye.xmemcached.impl.ArrayMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import net.rubyeye.xmemcached.transcoders.Transcoder;

import com.google.code.yanf4j.config.Configuration;

/**
 * Builder pattern.Configure XmemcachedClient's options,then build it
 * 
 * @author dennis
 * 
 */
public class XMemcachedClientBuilder implements MemcachedClientBuilder {

	private MemcachedSessionLocator sessionLocator = new ArrayMemcachedSessionLocator();
	private BufferAllocator bufferAllocator = new SimpleBufferAllocator();
	private Configuration configuration = XMemcachedClient
			.getDefaultConfiguration();
	private List<InetSocketAddress> addressList = new ArrayList<InetSocketAddress>();

	private int[] weights;

	private int poolSize = MemcachedClient.DEFAULT_POOL_SIZE;

	private List<MemcachedClientStateListener> stateListeners = new ArrayList<MemcachedClientStateListener>();

	@Override
	public void addStateListener(MemcachedClientStateListener stateListener) {
		this.stateListeners.add(stateListener);
	}

	public final void setPoolSize(int poolSize) {
		if (this.poolSize <= 0) {
			throw new IllegalArgumentException("poolSize<=0");
		}
		this.poolSize = poolSize;
	}

	@Override
	public void removeStateListener(MemcachedClientStateListener stateListener) {
		this.stateListeners.remove(stateListener);
	}

	@Override
	public void setStateListeners(
			List<MemcachedClientStateListener> stateListeners) {
		if (stateListeners == null) {
			throw new IllegalArgumentException("Null state listeners");
		}
		this.stateListeners = stateListeners;
	}

	private CommandFactory commandFactory = new TextCommandFactory();

	public final CommandFactory getCommandFactory() {
		return this.commandFactory;
	}

	public final void setCommandFactory(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}

	private @SuppressWarnings("unchecked")
	Transcoder transcoder = new SerializingTranscoder();

	public XMemcachedClientBuilder(List<InetSocketAddress> addressList) {
		this.addressList = addressList;
	}

	public XMemcachedClientBuilder(List<InetSocketAddress> addressList,
			int[] weights) {
		this.addressList = addressList;
		this.weights = weights;
	}

	public XMemcachedClientBuilder() {
		this(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rubyeye.xmemcached.MemcachedClientBuilder#getSessionLocator()
	 */
	public MemcachedSessionLocator getSessionLocator() {
		return this.sessionLocator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.rubyeye.xmemcached.MemcachedClientBuilder#setSessionLocator(net.rubyeye
	 * .xmemcached.MemcachedSessionLocator)
	 */
	public void setSessionLocator(MemcachedSessionLocator sessionLocator) {
		if (sessionLocator == null) {
			throw new IllegalArgumentException("Null SessionLocator");
		}
		this.sessionLocator = sessionLocator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rubyeye.xmemcached.MemcachedClientBuilder#getBufferAllocator()
	 */
	public BufferAllocator getBufferAllocator() {
		return this.bufferAllocator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.rubyeye.xmemcached.MemcachedClientBuilder#setBufferAllocator(net.
	 * rubyeye.xmemcached.buffer.BufferAllocator)
	 */
	public void setBufferAllocator(BufferAllocator bufferAllocator) {
		if (bufferAllocator == null) {
			throw new IllegalArgumentException("Null bufferAllocator");
		}
		this.bufferAllocator = bufferAllocator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rubyeye.xmemcached.MemcachedClientBuilder#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.rubyeye.xmemcached.MemcachedClientBuilder#setConfiguration(com.google
	 * .code.yanf4j.config.Configuration)
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rubyeye.xmemcached.MemcachedClientBuilder#build()
	 */
	public MemcachedClient build() throws IOException {
		if (this.weights == null) {
			return new XMemcachedClient(this.sessionLocator,
					this.bufferAllocator, this.configuration,
					this.commandFactory, this.transcoder, this.addressList,
					this.stateListeners, this.poolSize);
		} else {
			if (this.addressList == null) {
				throw new IllegalArgumentException("Null Address List");
			}
			if (this.addressList.size() > this.weights.length) {
				throw new IllegalArgumentException(
						"Weights Array's length is less than server's number");
			}
			return new XMemcachedClient(this.sessionLocator,
					this.bufferAllocator, this.configuration,
					this.commandFactory, this.transcoder, this.addressList,
					this.weights, this.stateListeners, this.poolSize);
		}
	}

	@SuppressWarnings("unchecked")
	public Transcoder getTranscoder() {
		return this.transcoder;
	}

	@SuppressWarnings("unchecked")
	public void setTranscoder(Transcoder transcoder) {
		if (transcoder == null) {
			throw new IllegalArgumentException("Null Transcoder");
		}
		this.transcoder = transcoder;
	}

}