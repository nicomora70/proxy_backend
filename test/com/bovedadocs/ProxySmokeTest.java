package com.bovedadocs;

import com.bovedadocs.audit.AuditEntry;
import com.bovedadocs.model.DocumentContent;
import com.bovedadocs.model.DocumentSummary;
import com.bovedadocs.model.User;
import com.bovedadocs.service.AccessDeniedException;
import com.bovedadocs.service.DocumentService;
import com.bovedadocs.service.RequestContext;
import com.bovedadocs.store.AppState;

import java.util.List;

public class ProxySmokeTest {
    public static void main(String[] args) {
        AppState state = new AppState();
        User laura = state.user("u1");
        User andres = state.user("u2");
        User carlos = state.user("u4");

        DocumentService full = state.chain("log,access,cache,lazy");
        List<DocumentSummary> lauraDocuments = full.list(laura, new RequestContext());
        check(lauraDocuments.size() == 3, "Laura should see 3 documents");
        check(!contains(lauraDocuments, "d2"), "Laura must not see confidential d2");

        boolean denied = false;
        try {
            full.read(laura, "d2", new RequestContext());
        } catch (AccessDeniedException expected) {
            denied = true;
        }
        check(denied, "Laura must not read d2");
        check(hasDeniedAudit(state), "Denied read must be audited");

        RequestContext firstRead = new RequestContext();
        DocumentContent first = full.read(andres, "d1", firstRead);
        check(first.getContent().length() > 0, "Andres should read d1");
        check(!firstRead.isCacheHit(), "First read must miss the cache");

        RequestContext secondRead = new RequestContext();
        full.read(andres, "d1", secondRead);
        check(secondRead.isCacheHit(), "Second read must hit the cache");

        state.reset();
        DocumentService lazyOnly = state.chain("log,access,lazy");
        RequestContext firstLazy = new RequestContext();
        lazyOnly.read(andres, "d7", firstLazy);
        check(!firstLazy.isLazyReused(), "First lazy read must load content");
        RequestContext secondLazy = new RequestContext();
        lazyOnly.read(andres, "d7", secondLazy);
        check(secondLazy.isLazyReused(), "Second lazy read must reuse content");

        state.reset();
        DocumentService withoutAccess = state.chain("log,lazy");
        withoutAccess.delete(laura, "d1", new RequestContext());
        check(state.chain("log,access").list(carlos, new RequestContext()).size() == 7,
                "Without the access proxy Laura can delete d1");

        state.reset();
        state.chain("log,access").delete(carlos, "d6", new RequestContext());
        check(state.chain("log,access").list(carlos, new RequestContext()).size() == 7,
                "Admin should delete d6");

        System.out.println("ProxySmokeTest: all checks passed");
    }

    private static boolean contains(List<DocumentSummary> documents, String id) {
        for (DocumentSummary document : documents) {
            if (document.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasDeniedAudit(AppState state) {
        for (AuditEntry entry : state.audit().last(20)) {
            if ("DENIED".equals(entry.getStatus())) {
                return true;
            }
        }
        return false;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
