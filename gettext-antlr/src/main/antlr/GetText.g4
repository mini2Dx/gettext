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
    : entries NEWLINE* EOF
    ;

entries
    : entry+
    ;

entry
    : emptyLine* commentsBlock? messagesBlock
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
    : messageExpression (NEWLINE messagesBlock)+
    | messageExpression
    ;

messageExpression
    : messageIdPlural
    | messageId
    | messageContext
    | messageNumStr (NEWLINE messageNumStr)*
    | messageStr
    ;

messageNumStr
    : MSGSTR WHITESPACE? numericIndexLiteral WHITESPACE+ quotedTextLiteral (NEWLINE WHITESPACE? quotedTextLiteral)+
    | MSGSTR WHITESPACE? numericIndexLiteral WHITESPACE+ quotedTextLiteral
    | MSGSTR WHITESPACE? numericIndexLiteral WHITESPACE+ unquotedTextLiteral
    ;

messageStr
    : MSGSTR WHITESPACE+ quotedTextLiteral (NEWLINE WHITESPACE? quotedTextLiteral)+
    | MSGSTR WHITESPACE+ quotedTextLiteral
    | MSGSTR WHITESPACE unquotedTextLiteral
    ;

messageIdPlural
    : MSGID_PLURAL WHITESPACE+ quotedTextLiteral (NEWLINE WHITESPACE* quotedTextLiteral)+
    | MSGID_PLURAL WHITESPACE+ quotedTextLiteral
    | MSGID_PLURAL WHITESPACE unquotedTextLiteral
    ;

messageId
    : MSGID WHITESPACE+ quotedTextLiteral (NEWLINE WHITESPACE* quotedTextLiteral)+
    | MSGID WHITESPACE+ quotedTextLiteral
    | MSGID WHITESPACE unquotedTextLiteral
    ;

messageContext
    : MSGCTXT WHITESPACE+ quotedTextLiteral (NEWLINE WHITESPACE* quotedTextLiteral)+
    | MSGCTXT WHITESPACE+ quotedTextLiteral
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
    : ('\\"' | unquotedTextChar)+
    ;

unquotedTextChar
    : TEXT
    | otherChar
    ;

quotedTextLiteral
    : DOUBLEQUOTE ('\\"' | quotedTextChar)* DOUBLEQUOTE
    ;

quotedTextChar
    : TEXT
    | NEWLINE
    | otherChar
    ;

emptyLine
    : WHITESPACE* NEWLINE
    ;

otherChar
    : WHITESPACE
    | HASH
    | FULLSTOP
    | COLON
    | COMMA
    | PIPE
    | LEFTBRACKET
    | RIGHTBRACKET
    | DIGIT
    | MSGCTXT
    | MSGSTR
    | MSGID
    | MSGID_PLURAL
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

TEXT: ~[\r\n"];