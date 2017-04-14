/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016-2017 Peter Keeler
 *
 * This file is part of EmergentMUD.
 *
 * EmergentMUD is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EmergentMUD is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.emergentmud.core.config;

import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Properties;

@Configuration
public class HttpConfiguration {
    @Bean
    public FreeMarkerConfigurer configureFreemarker() {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        Properties settings = new Properties();

        settings.setProperty(freemarker.template.Configuration.TEMPLATE_EXCEPTION_HANDLER_KEY, "rethrow");
        freeMarkerConfigurer.setFreemarkerSettings(settings);
        freeMarkerConfigurer.setTemplateLoaderPath(FreeMarkerProperties.DEFAULT_TEMPLATE_LOADER_PATH);

        return freeMarkerConfigurer;
    }
}
