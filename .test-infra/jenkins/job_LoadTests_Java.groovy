/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import CommonJobProperties as commonJobProperties
import LoadTestsBuilder as loadTestsBuilder
import PhraseTriggeringPostCommitBuilder

def testsConfigurations = [
        [
                jobName           : 'beam_Java_LoadTests_GroupByKey_Direct_Small',
                jobDescription    : 'Runs GroupByKey load tests on direct runner small records 10b',
                itClass           : 'org.apache.beam.sdk.loadtests.GroupByKeyLoadTest',
                prCommitStatusName: 'Java GroupByKey Small Java Load Test Direct',
                prTriggerPhrase   : 'Run GroupByKey Small Java Load Test Direct',
                runner            : CommonTestProperties.Runner.DIRECT,
                jobProperties     : [
                        publishToBigQuery: true,
                        bigQueryDataset  : 'load_test_PRs',
                        bigQueryTable    : 'direct_gbk_small',
                        sourceOptions    : '{"numRecords":1000000000,"splitPointFrequencyRecords":1,"keySizeBytes":1,"valueSizeBytes":9,"numHotKeys":0,"hotKeyFraction":0,"seed":123456,"bundleSizeDistribution":{"type":"const","const":42},"forceNumInitialBundles":100,"progressShape":"LINEAR","initializeDelayDistribution":{"type":"const","const":42}}',
                        stepOptions      : '{"outputRecordsPerInputRecord":1,"preservesInputKeyDistribution":true,"perBundleDelay":10000,"perBundleDelayType":"MIXED","cpuUtilizationInMixedDelay":0.5}',
                        fanout           : 10,
                        iterations       : 1,
                ]

        ],
        [
                jobName           : 'beam_Java_LoadTests_GroupByKey_Dataflow_Small',
                jobDescription    : 'Runs GroupByKey load tests on Dataflow runner small records 10b',
                itClass           : 'org.apache.beam.sdk.loadtests.GroupByKeyLoadTest',
                prCommitStatusName: 'Java GroupByKey Small Load Test Dataflow',
                prTriggerPhrase   : 'Run GroupByKey Small Java Load Test Dataflow',
                runner            : CommonTestProperties.Runner.DATAFLOW,
                jobProperties     : [
                        publishToBigQuery   : true,
                        bigQueryDataset     : 'load_test_PRs',
                        bigQueryTable       : 'dataflow_gbk_small',
                        sourceOptions       : '{"numRecords":1000000000,"splitPointFrequencyRecords":1,"keySizeBytes":1,"valueSizeBytes":9,"numHotKeys":0,"hotKeyFraction":0,"seed":123456,"bundleSizeDistribution":{"type":"const","const":42},"forceNumInitialBundles":100,"progressShape":"LINEAR","initializeDelayDistribution":{"type":"const","const":42}}',
                        stepOptions         : '{"outputRecordsPerInputRecord":1,"preservesInputKeyDistribution":true,"perBundleDelay":10000,"perBundleDelayType":"MIXED","cpuUtilizationInMixedDelay":0.5}',
                        fanout              : 10,
                        iterations          : 1,
                        maxNumWorkers       : 10,
                ]

        ],
]

for (testConfiguration in testsConfigurations) {
    PhraseTriggeringPostCommitBuilder.postCommitJob(
            testConfiguration.jobName,
            testConfiguration.prTriggerPhrase,
            testConfiguration.prCommitStatusName,
            this
    ) {
        description(testConfiguration.jobDescription)
        commonJobProperties.setTopLevelMainJobProperties(delegate)
        loadTestsBuilder.buildTest(delegate, testConfiguration.jobDescription, testConfiguration.runner, testConfiguration.jobProperties, testConfiguration.itClass)
    }
}