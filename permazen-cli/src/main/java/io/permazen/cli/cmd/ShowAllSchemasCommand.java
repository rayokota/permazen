
/*
 * Copyright (C) 2015 Archie L. Cobbs. All rights reserved.
 */

package io.permazen.cli.cmd;

import io.permazen.cli.CliSession;
import io.permazen.core.Schema;
import io.permazen.core.Transaction;
import io.permazen.schema.SchemaModel;
import io.permazen.util.ParseContext;

import java.util.Map;

public class ShowAllSchemasCommand extends AbstractSchemaCommand {

    public ShowAllSchemasCommand() {
        super("show-all-schemas -x:xml");
    }

    @Override
    public String getHelpSummary() {
        return "Shows all schema versions recorded in the database";
    }

    @Override
    public String getHelpDetail() {
        return "If the `-x' flag is provided, the XML representation of each schema version is included.";
    }

    @Override
    public CliSession.Action getAction(CliSession session, ParseContext ctx, boolean complete, Map<String, Object> params) {
        final boolean xml = params.containsKey("xml");
        return new ShowSchemasAction(xml);
    }

    private static class ShowSchemasAction implements CliSession.Action {

        private final boolean xml;

        ShowSchemasAction(boolean xml) {
            this.xml = xml;
        }

        @Override
        public void run(CliSession session) throws Exception {
            AbstractSchemaCommand.runWithoutSchema(session, new SchemaAgnosticAction<Void>() {
                @Override
                public Void runWithoutSchema(CliSession session, Transaction tx) {
                    for (Map.Entry<Integer, Schema> entry : tx.getSchemas().getVersions().entrySet()) {
                        final int number = entry.getKey();
                        final SchemaModel model = entry.getValue().getSchemaModel();
                        if (ShowSchemasAction.this.xml) {
                            session.getWriter().println("=== Schema version " + number + " ===\n"
                              + model.toString().replaceAll("^<.xml[^>]+>\\n", ""));
                        } else
                            session.getWriter().println(number);
                    }
                    return null;
                }
            });
        }
    }
}

