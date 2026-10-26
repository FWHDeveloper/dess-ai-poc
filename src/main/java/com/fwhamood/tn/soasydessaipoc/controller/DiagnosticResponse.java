package com.fwhamood.tn.soasydessaipoc.controller;

import java.util.List;

public record DiagnosticResponse(String answer,
                                 List<SourceRef> sources,
                                 String mode) {
}


