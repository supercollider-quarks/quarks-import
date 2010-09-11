
// this fixes missing update with audio rate mapping

+ NodeProxy {
	
	put { | index, obj, channelOffset = 0, extraArgs, now = true |			
			var container, bundle, orderIndex;
			if(obj.isNil) { this.removeAt(index); ^this };
			if(index.isSequenceableCollection) { 						^this.putAll(obj.asArray, index, channelOffset)
			};

			orderIndex = index ? 0;
			container = obj.makeProxyControl(channelOffset, this);
			container.build(this, orderIndex); // bus allocation happens here

			if(this.shouldAddObject(container, index)) {
				bundle = MixedBundle.new;
				if(index.isNil)
					{ this.removeAllToBundle(bundle) }
					{ this.removeToBundle(bundle, index) };
				objects = objects.put(orderIndex, container);
			} {
				format("failed to add % to node proxy: %", obj, this).inform;
				^this
			};

			if(server.serverRunning) {
				now = awake && now;
				if(now) {
					this.prepareToBundle(nil, bundle);
				};
				container.loadToBundle(bundle, server);
				loaded = true;
				if(now) {
					container.wakeUpParentsToBundle(bundle);
					this.sendObjectToBundle(bundle, container, extraArgs, index);
				};
				nodeMap.wakeUpParentsToBundle(bundle); // bugfix: wake up mapped audio rate proxies
				bundle.schedSend(server, clock ? TempoClock.default, quant);
			} {
				loaded = false;
			}

	}	
	
	
}

