package sv.edu.ues.occ.ingenieria.ppi115_2026.pos.cafefe.boundary.jsf.rest;

/**
 *
 * @author mil
 */

public enum ResponseHeaders {
    
    PROCESS_ERROR("Process-Error"),
    APP_ERROR("App-Error"),
    WRONG_PARAMETER("Wrong-Parameter"),
    WRONG_PARAM("Wrong-Param"),
    NOT_FOUND("Not-Found"),
    TOTAL_RECORDS("Total-Records"),
    TOTAL_REGISTROS("Total-Records"),
    VIOLATES_BUSINESS_RULES("Violates-Business-Rules");
    
    private final String headerName;
    
    ResponseHeaders(String headerName) {
        this.headerName = headerName;
    }
    
    public String getHeaderName() {
        return headerName;
    }
    
    @Override
    public String toString() {
        return headerName;
    }
}
