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

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Immutable thread-safe Quote record, without the ID.
 * <p>
 * In principle Java records can be used with Jsonb, although there are some limitations.
 * See <a href="https://carloschac.in/2020/04/20/java-records-jsonb/">Java records Jsonb</a> for more
 * on Jsonb-enabled Java records. It mentions the need for overriding the PropertyVisibilityStrategy,
 * and the need to use a JsonbCreator annotation for deserialization. Here we would have additional
 * challenges, such as the use of Guava immutable collections and problems with nested records
 * (with the outer record representing a collection of Quote records).
 * <p>
 * Alternatively, we could support JSON (de)serialization using the lower level Jsonp API.
 * Another alternative is plain indirection, using Jsonb proxy types. The latter approach has been
 * followed here.
 *
 * @author Chris de Vreeze
 */
public record QuoteData(
        String quoteText,
        String attributedTo,
        ImmutableSet<String> subjects
) {

    public JsonbProxy toJsonbProxy() {
        var jsonbProxy = new JsonbProxy();
        jsonbProxy.setQuoteText(quoteText());
        jsonbProxy.setAttributedTo(attributedTo());
        jsonbProxy.setSubjects(Set.copyOf(subjects()));
        return jsonbProxy;
    }

    public static QuoteData fromJsonbProxy(JsonbProxy jsonbProxy) {
        return new QuoteData(
                jsonbProxy.getQuoteText(),
                jsonbProxy.getAttributedTo(),
                ImmutableSet.copyOf(jsonbProxy.getSubjects())
        );
    }

    public static final class JsonbProxy {

        private String quoteText;
        private String attributedTo;
        private Set<String> subjects;

        public String getQuoteText() {
            return quoteText;
        }

        public void setQuoteText(String quoteText) {
            this.quoteText = quoteText;
        }

        public String getAttributedTo() {
            return attributedTo;
        }

        public void setAttributedTo(String attributedTo) {
            this.attributedTo = attributedTo;
        }

        public Set<String> getSubjects() {
            return subjects;
        }

        public void setSubjects(Set<String> subjects) {
            this.subjects = subjects;
        }
    }
}
