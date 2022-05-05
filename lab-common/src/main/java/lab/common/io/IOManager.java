package lab.common.io;

public class IOManager<R, W> {

    private Reader<R> reader;
    private Writer<W> writter;

    public IOManager() {

    }

    public IOManager(Reader<R> reader, Writer<W> writer) {
        this.reader = reader;
        this.writter = writer;
    }

    public Reader<R> getReader() {
        return reader;
    }

    public void setReader(Reader<R> reader) {
        this.reader = reader;
    }

    public Writer<W> getWriter() {
        return writter;
    }

    public void setWriter(Writer<W> writer) {
        this.writter = writer;
    }

    public void setIO(Reader<R> newReader, Writer<W> newWriter) {
        this.setReader(newReader);
        this.setWriter(newWriter);
    }

    public void write(W message) {
        writter.write(message);
    }

    public R read() {
        return reader.read();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((reader == null) ? 0 : reader.hashCode());
        result = prime * result + ((writter == null) ? 0 : writter.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IOManager<?, ?> other = (IOManager<?, ?>) obj;
        if (reader == null) {
            if (other.reader != null) {
                return false;
            }
        } else if (!reader.equals(other.reader)) {
            return false;
        }
        if (writter == null) {
            return other.writter == null;
        }
        return writter.equals(other.writter);
    }

}
