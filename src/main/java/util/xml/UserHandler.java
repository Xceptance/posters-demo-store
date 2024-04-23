/*
 * Copyright (c) 2013-2024 Xceptance Software Technologies GmbH
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
package util.xml;

import models_backoffice.Backofficeuser;


import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This {@link DefaultHandler} parses an XML file with user data and persists them in the database.
 * 
 * @author kennygozali
 */
public class UserHandler extends DefaultHandler
{

    private Backofficeuser user;
    private String currentValue;

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) {
        if (localName.equals("backofficeuser")) {
            user = new Backofficeuser();
            user.save();
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) {
        if (localName.equals("backofficeuser")) {
            user.update();
        }
        if (localName.equals("email")) {
            user.setEmail(currentValue);
        }
        if (localName.equals("password")) {
            user.hashPasswd(currentValue);
        }
        if (localName.equals("name")) {
            user.setName(currentValue);
        }
        if (localName.equals("firstName")) {
            user.setFirstName(currentValue);
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) {
        currentValue = new String(ch, start, length);
    }
}
