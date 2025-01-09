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

package eu.cdevreeze.tryopenliberty.quoteswebapp.infra;

import eu.cdevreeze.tryopenliberty.quoteswebapp.cdi.annotation.QuoteDataSource;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Typed;

import javax.sql.DataSource;

/**
 * CDI Producer to make the DataSource injectable, that is, to turn a DataSource Resource into an injectable
 * CDI bean.
 * <p>
 * See <a href="https://antoniogoncalves.org/2011/09/25/injection-with-cdi-part-iii/">Injection with CDI part 3</a>.
 *
 * @author Chris de Vreeze
 */
@Dependent
public class DataSourceProducer {

    @Produces
    @QuoteDataSource
    @Typed({DataSource.class})
    @Resource(name = "jdbc/quoteDataSource")
    private DataSource dataSource;
}
