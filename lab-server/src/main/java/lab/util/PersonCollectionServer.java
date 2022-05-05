package lab.util;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import lab.common.commands.CommandResponse;
import lab.common.commands.CommandResult;
import lab.common.io.IOManager;
import lab.common.util.CommandRunner;
import lab.io.ServerExecuteRequest;
import lab.io.ServerResponse;

public final class PersonCollectionServer {

    private static final int READER_POOL_SIZE = 2;
    private static final int RUNNER_POOL_SIZE = 4;

    private final CommandRunner<String, CommandResponse> serverCommandRunner;
    private final CommandRunner<ServerExecuteRequest, ServerResponse> serverToClientCommandRunner;
    private final IOManager<ServerExecuteRequest, ServerResponse> io;
    private final ForkJoinPool readerPool;
    private boolean stopped;
    private final ExecutorService runnerPool;
    private final BlockingQueue<ServerExecuteRequest> requestQueue;
    private final ForkJoinPool writterPool;
    private final BlockingQueue<ServerResponse> responseQueue;

    public PersonCollectionServer(CommandRunner<String, CommandResponse> serverCommandRunner,
            CommandRunner<ServerExecuteRequest, ServerResponse> serverToClientCommandRunner) {
        this.serverCommandRunner = serverCommandRunner;
        this.serverToClientCommandRunner = serverToClientCommandRunner;
        this.io = serverToClientCommandRunner.getIO();
        this.readerPool = new ForkJoinPool();
        this.runnerPool = Executors.newFixedThreadPool(RUNNER_POOL_SIZE);
        this.requestQueue = new ArrayBlockingQueue<>(RUNNER_POOL_SIZE);
        this.writterPool = new ForkJoinPool();
        this.responseQueue = new ArrayBlockingQueue<>(RUNNER_POOL_SIZE);
    }

    public void run() {
        stopped = false;

        readerPool.submit(this::startReaders);
        // startRunners();
        // writterPool.submit(this::startWritters);

        CommandResponse resp;
        do {
            resp = serverCommandRunner.runNextCommand();
        } while (resp.getResult() != CommandResult.END);

        stopped = true;
        runnerPool.shutdown();
    }

    private void startReaders() {
        while (!stopped && readerPool.getQueuedTaskCount() < READER_POOL_SIZE) {
            readNext();
        }
    }

    private void readNext() {
        ServerExecuteRequest nextCommand;
        nextCommand = io.read();
        if (Objects.nonNull(nextCommand)) {
            try {
                requestQueue.put(nextCommand);
                runnerPool.execute(this::runNext);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void runNext() {
        try {
            responseQueue.add(
                    serverToClientCommandRunner.run(requestQueue.take()));
            writterPool.submit(this::writeNext);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void writeNext() {
        try {
            io.write(responseQueue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
