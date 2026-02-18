package service;
import dataaccess.ClearDoa;

public class ClearService {
    public void clear() {
        ClearDoa doa = new ClearDoa();
        doa.clearDatabases();
    }
}
