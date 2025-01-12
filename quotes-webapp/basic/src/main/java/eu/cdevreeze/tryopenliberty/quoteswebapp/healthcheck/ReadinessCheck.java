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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.tryopenliberty.quoteswebapp.model.Quote;
import eu.cdevreeze.tryopenliberty.quoteswebapp.service.QuoteService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 * Readiness health check. Use path "health/ready" to access it.
 *
 * @author Chris de Vreeze
 */
@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck {

    private final QuoteService quoteService;

    @Inject
    public ReadinessCheck(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @Override
    public HealthCheckResponse call() {
        ImmutableList<Quote> quotes = quoteService.findAllQuotes();
        Preconditions.checkArgument(!quotes.isEmpty());

        var msg = ReadinessCheck.class.getSimpleName() + " Readiness Check";
        return HealthCheckResponse.up(msg);
    }
}
