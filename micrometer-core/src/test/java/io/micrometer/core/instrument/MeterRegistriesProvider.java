/**
 * Copyright 2017 Pivotal Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.core.instrument;

import com.netflix.spectator.api.DefaultRegistry;
import io.micrometer.core.instrument.datadog.DatadogConfig;
import io.micrometer.core.instrument.datadog.DatadogMeterRegistry;
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry;
import io.micrometer.core.instrument.prometheus.PrometheusMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.instrument.spectator.SpectatorMeterRegistry;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.prometheus.client.CollectorRegistry;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.time.Duration;
import java.util.stream.Stream;

class MeterRegistriesProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
                (Object) new SpectatorMeterRegistry(new DefaultRegistry(), new MockClock()),
                new PrometheusMeterRegistry(new CollectorRegistry(true), new MockClock()),
                new SimpleMeterRegistry(new MockClock()),
                new DatadogMeterRegistry(new MockClock(), new DatadogConfig() {
                    @Override
                    public boolean enabled() {
                        return false;
                    }

                    @Override
                    public String apiKey() {
                        return "DOESNOTMATTER";
                    }

                    @Override
                    public String get(String k) {
                        return null;
                    }

                    @Override
                    public Duration step() {
                        return Duration.ofSeconds(1);
                    }
                }),
                new DropwizardMeterRegistry(new HierarchicalNameMapper(), new MockClock())
        ).map(Arguments::of);
    }
}
