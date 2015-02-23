/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     
 */

package org.nuxeo.labs.operations.document;

import java.io.IOException;
import java.util.List;

import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.automation.core.util.ComplexTypeJSONDecoder;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.schema.types.ListType;

/**
 * @author fvadon
 */
@Operation(id = AddEntryToComplexProperties.ID, category = Constants.CAT_DOCUMENT, label = "Add Complex Property From Json String", description = "This operation can add new fields to a multivalued complex metadata. The value parameter is a String containing the JSON list of new value for the metadata given in xpath")
public class AddEntryToComplexProperties {

    public static final String ID = "AddComplexProperty";

    @Context
    protected CoreSession session;

    @Context
    protected AutomationService service;

    @Context
    protected OperationContext ctx;

    @Param(name = "xpath")
    protected String xpath;

    @Param(name = "ComplexJsonProperties")
    protected String ComplexJsonProperties;

    @Param(name = "save", required = false, values = { "true" })
    protected boolean save = true;

    @OperationMethod(collector = DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) throws OperationException,
            IOException {
        Property complexMeta = doc.getProperty(xpath);
        ListType ltype = (ListType) complexMeta.getField().getType();

        if (!ltype.getFieldType().isComplexType()) {
            throw new OperationException(
                    "Property type is not supported by this operation");
        }

        List<Object> newVals = ComplexTypeJSONDecoder.decodeList(ltype,
                ComplexJsonProperties);
        for (Object newVal : newVals) {
            complexMeta.addValue(newVal);
        }

        if (save) {
            doc = session.saveDocument(doc);
            session.save();
        }
        return doc;
    }

}
