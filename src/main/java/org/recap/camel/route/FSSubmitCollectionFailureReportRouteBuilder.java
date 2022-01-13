package org.recap.camel.route;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.csv.SubmitCollectionReportRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by rajeshbabuk on 20/7/17.
 */
@Slf4j
@Component
public class FSSubmitCollectionFailureReportRouteBuilder {

    /**
     * This method instantiates a new route builder for generating submit collection failure report to the file system.
     *
     * @param context          the context
     * @param reportsDirectory the reports directory
     */
    @Autowired
    public FSSubmitCollectionFailureReportRouteBuilder(CamelContext context, @Value("${" + PropertyKeyConstants.SUBMIT_COLLECTION_REPORT_DIRECTORY + "}") String reportsDirectory) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(ScsbConstants.FS_SUBMIT_COLLECTION_FAILURE_REPORT_Q)
                            .routeId(ScsbConstants.FS_SUBMIT_COLLECTION_FAILURE_REPORT_ID)
                            .marshal().bindy(BindyType.Csv, SubmitCollectionReportRecord.class)
                            .to("file:" + reportsDirectory + File.separator + "?fileName=${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv&fileExist=append");
                }
            });
        } catch (Exception e) {
            log.error(ScsbCommonConstants.LOG_ERROR,e);
        }
    }
}
