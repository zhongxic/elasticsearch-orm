package org.example.elasticsearch.orm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.search.aggregations.bucket.ParsedSingleBucketAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.ParsedSingleValueNumericMetricsAggregation;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AggregationUtil {

    public static long multiBucketsAggregationBucketSize(Terms terms) {
        return terms.getBuckets().size();
    }

    public static String multiBucketsAggregationFirstBucketKey(Terms terms) {
        if (CollectionUtils.isEmpty(terms.getBuckets())) {
            return null;
        }
        return terms.getBuckets().get(0).getKeyAsString();
    }

    public static List<String> multiBucketsAggregationAllBucketKeys(Terms terms) {
        return terms.getBuckets().stream().map(Bucket::getKeyAsString).collect(Collectors.toList());
    }

    public static String multiBucketsAggregationBucketKey(Bucket bucket) {
        return bucket.getKeyAsString();
    }

    public static long multiBucketsAggregationBucketDocCount(Bucket bucket) {
        return bucket.getDocCount();
    }

    public static long parsedSingleBucketAggregationDocCount(ParsedSingleBucketAggregation aggregation) {
        return aggregation.getDocCount();
    }

    public static double parsedSingleValueNumericMetricsAggregationValue(ParsedSingleValueNumericMetricsAggregation aggregation) {
        return aggregation.value();
    }

}