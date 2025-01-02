/*
 * Copyright 2024-2024 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.tryopenliberty.quoteswebapp.healthcheck;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * Liveness health check. Use path "health/live" to access it.
 *
 * @author Chris de Vreeze
 */
@Liveness
@ApplicationScoped
public class LivenessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        long heapMemoryUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
        long heapMemoryMax = memoryMXBean.getHeapMemoryUsage().getMax();
        boolean livenessCheckUp = heapMemoryUsed < heapMemoryMax * 0.9;

        var msg = LivenessCheck.class.getSimpleName() + " Liveness Check";
        return HealthCheckResponse.named(msg).status(livenessCheckUp).build();
    }
}
