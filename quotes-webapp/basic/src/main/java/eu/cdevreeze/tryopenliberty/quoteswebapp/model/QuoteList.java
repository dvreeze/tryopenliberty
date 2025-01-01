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

package eu.cdevreeze.tryopenliberty.quoteswebapp.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Quote collection. Its main feature is its Jsonb (de)serialization proxy.
 *
 * @author Chris de Vreeze
 */
public record QuoteList(ImmutableList<Quote> quotes) {

    public JsonbProxy toJsonbProxy() {
        var jsonbProxy = new JsonbProxy();
        jsonbProxy.setQuotes(quotes.stream().map(Quote::toJsonbProxy).toList());
        return jsonbProxy;
    }

    public static QuoteList fromJsonbProxy(JsonbProxy jsonbProxy) {
        return new QuoteList(
                jsonbProxy.getQuotes()
                        .stream()
                        .map(Quote::fromJsonbProxy)
                        .collect(ImmutableList.toImmutableList())
        );
    }

    public static final class JsonbProxy {

        private List<Quote.JsonbProxy> quotes;

        public List<Quote.JsonbProxy> getQuotes() {
            return quotes;
        }

        public void setQuotes(List<Quote.JsonbProxy> quotes) {
            this.quotes = quotes;
        }
    }
}
