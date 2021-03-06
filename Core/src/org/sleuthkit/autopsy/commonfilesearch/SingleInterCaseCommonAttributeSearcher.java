/*
 *
 * Autopsy Forensic Browser
 *
 * Copyright 2018 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
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
package org.sleuthkit.autopsy.commonfilesearch;

import java.sql.SQLException;
import java.util.Map;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.casemodule.NoCurrentCaseException;
import org.sleuthkit.autopsy.centralrepository.datamodel.CorrelationCase;
import org.sleuthkit.autopsy.centralrepository.datamodel.EamDbException;
import org.sleuthkit.datamodel.TskCoreException;
import org.sleuthkit.autopsy.centralrepository.datamodel.CorrelationAttributeInstance.Type;

/**
 *
 *
 */
public class SingleInterCaseCommonAttributeSearcher extends InterCaseCommonAttributeSearcher {

    private final int corrleationCaseId;
    private String correlationCaseName;

    /**
     *
     * @param correlationCaseId
     * @param filterByMediaMimeType
     * @param filterByDocMimeType
     * @param corAttrType
     * @param percentageThreshold
     *
     * @throws EamDbException
     */
    public SingleInterCaseCommonAttributeSearcher(int correlationCaseId, boolean filterByMediaMimeType,
            boolean filterByDocMimeType, Type corAttrType, int percentageThreshold) throws EamDbException {
        super(filterByMediaMimeType, filterByDocMimeType, corAttrType, percentageThreshold);

        this.corrleationCaseId = correlationCaseId;
        this.correlationCaseName = "";
    }

    /**
     * Collect metadata required to render the tree table where matches must
     * occur in the case with the given ID.
     *
     * @return business object needed to populate tree table with results
     *
     * @throws TskCoreException
     * @throws NoCurrentCaseException
     * @throws SQLException
     * @throws EamDbException
     */
    @Override
    public CommonAttributeSearchResults findMatches() throws TskCoreException, NoCurrentCaseException, SQLException, EamDbException {

        CorrelationCase cCase = this.getCorrelationCaseFromId(this.corrleationCaseId);
        this.correlationCaseName = cCase.getDisplayName();
        return this.findFiles(cCase);
    }

    CommonAttributeSearchResults findFiles(CorrelationCase correlationCase) throws TskCoreException, NoCurrentCaseException, SQLException, EamDbException {
        InterCaseSearchResultsProcessor eamDbAttrInst = new InterCaseSearchResultsProcessor(this.corAttrType);
        Map<Integer, CommonAttributeValueList> interCaseCommonFiles = eamDbAttrInst.findSingleInterCaseCommonAttributeValues(Case.getCurrentCase(), correlationCase);

        return new CommonAttributeSearchResults(interCaseCommonFiles, this.frequencyPercentageThreshold, this.corAttrType);
    }

    @NbBundle.Messages({
        "# {0} - case name",
        "# {1} - attr type",
        "# {2} - threshold string",
        "SingleInterCaseCommonAttributeSearcher.buildTabTitle.titleInterSingle=Common Properties (Central Repository Case: {0}, {1}{2})"})

    @Override
    String getTabTitle() {
        return Bundle.SingleInterCaseCommonAttributeSearcher_buildTabTitle_titleInterSingle(this.correlationCaseName, this.corAttrType.getDisplayName(), this.getPercentThresholdString());
    }
}
