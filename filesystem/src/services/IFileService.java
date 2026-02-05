package filesystem.src.services;

public interface IFileService {
    public boolean mkdir(String path);

    public String pwd();

    public boolean cd(String path);
}
