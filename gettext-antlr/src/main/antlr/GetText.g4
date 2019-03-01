//
// Copyright 2019 Thomas Cashman
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
grammar GetText;

po
    : entry (emptyLine+ entry)* EOF
    ;

entry
    : commentsBlock? messagesBlock
    ;

commentsBlock
    : commentExpression+
    ;

commentExpression
    : reference NEWLINE
    | mergeComment NEWLINE
    | flag NEWLINE
    | extractedComment NEWLINE
    | translatorComment NEWLINE
    ;

messagesBlock
    : messageExpression+
    ;

messageExpression
    : messageIdPlural NEWLINE
    | messageId NEWLINE
    | messageContext NEWLINE
    | messageStr (NEWLINE messageStr)* NEWLINE
    ;

messageStr
    : MSGSTR WHITESPACE? numericIndexLiteral WHITESPACE+ QuotedTextLiteral (NEWLINE WHITESPACE? QuotedTextLiteral)*
    | MSGSTR WHITESPACE? numericIndexLiteral WHITESPACE+ unquotedTextLiteral
    | MSGSTR WHITESPACE+ QuotedTextLiteral (NEWLINE WHITESPACE? QuotedTextLiteral)*
    | MSGSTR WHITESPACE unquotedTextLiteral
    ;

messageIdPlural
    : MSGID_PLURAL WHITESPACE+ QuotedTextLiteral (NEWLINE WHITESPACE* QuotedTextLiteral)*
    | MSGID_PLURAL WHITESPACE unquotedTextLiteral
    ;

messageId
    : MSGID WHITESPACE+ QuotedTextLiteral (NEWLINE WHITESPACE+ QuotedTextLiteral)*
    | MSGID WHITESPACE unquotedTextLiteral
    ;

messageContext
    : MSGCTXT WHITESPACE+ QuotedTextLiteral (NEWLINE WHITESPACE+ QuotedTextLiteral)*
    | MSGCTXT WHITESPACE unquotedTextLiteral
    ;

reference
    : HASH COLON unquotedTextLiteral
    ;

mergeComment
    : HASH PIPE unquotedTextLiteral
    ;

flag
    : HASH COMMA unquotedTextLiteral
    ;

extractedComment
    : HASH FULLSTOP unquotedTextLiteral
    ;

translatorComment
    : HASH unquotedTextLiteral
    ;

numericIndexLiteral
    : LEFTBRACKET digits RIGHTBRACKET
    ;

digits
    : DIGIT+
    ;

unquotedTextLiteral
    : UnquotedTextChar+
    ;

QuotedTextLiteral
    : DOUBLEQUOTE QuotedTextChar+ DOUBLEQUOTE
    ;

emptyLine
    : WHITESPACE* NEWLINE
    ;

MSGCTXT : 'msgctxt';
MSGSTR : 'msgstr';
MSGID : 'msgid';
MSGID_PLURAL : 'msgid_plural';

HASH: '#';
FULLSTOP: '.';
COLON: ':';
COMMA: ',';
PIPE: '|';
DOUBLEQUOTE: '"';
LEFTBRACKET: '[';
RIGHTBRACKET: ']';
DIGIT: [0-9];

WHITESPACE: [ \t];
NEWLINE : ( '\r'? '\n' | '\r' );

fragment
QuotedTextChar
    : ~('"')
    ;

UnquotedTextChar
    : ~('\n'|'\r')
    | WHITESPACE
    | COLON
    | DIGIT
    | COMMA
    | FULLSTOP
    | LEFTBRACKET
    | RIGHTBRACKET
    ;