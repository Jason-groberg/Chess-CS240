package dataaccess;

public class ClearDoa {
    public void clearDatabases() {
        DataBases.authDatabase.clear();
        DataBases.userDatabase.clear();
        DataBases.gameDataBase.clear();
    }
}
