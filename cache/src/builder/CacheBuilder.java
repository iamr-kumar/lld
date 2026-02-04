package cache.src.builder;

import java.util.ArrayList;
import java.util.List;

import cache.src.core.ICacheLevel;
import cache.src.core.MultiLevelCache;
import cache.src.population.IPopulationStrategy;

public class CacheBuilder<K, V> {
    private int levels;
    private List<ICacheLevel<K, V>> cacheLevels;
    private IPopulationStrategy promotionStrategy;

    public CacheBuilder() {
        this.levels = 0;
        this.cacheLevels = new ArrayList<>();
        this.promotionStrategy = null;
    }

    public CacheBuilder<K, V> setLevels(int levels) {
        this.levels = levels;
        return this;
    }

    public CacheBuilder<K, V> addCacheLevel(ICacheLevel<K, V> cacheLevel) {
        if (this.cacheLevels.size() >= this.levels) {
            throw new IllegalStateException("Cannot add more levels than specified");
        }
        this.cacheLevels.add(cacheLevel);
        return this;
    }

    public CacheBuilder<K, V> setPromotionStrategy(IPopulationStrategy promotionStrategy) {
        this.promotionStrategy = promotionStrategy;
        return this;
    }

    public MultiLevelCache<K, V> build() {
        if (this.cacheLevels.size() != this.levels) {
            throw new IllegalStateException("Number of added levels does not match specified levels");
        }
        if (this.promotionStrategy == null) {
            throw new IllegalStateException("Promotion strategy must be set");
        }
        return new MultiLevelCache<K, V>(new ArrayList<>(this.cacheLevels), this.promotionStrategy);
    }

}
