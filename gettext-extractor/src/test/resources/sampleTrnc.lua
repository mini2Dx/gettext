npc:say(GetText:trnc("ctx0", "Simple", "Simples", 1))

npc:say(GetText:trnc("ctx1", "Multi " .. "part", "Multi " .. "parts", 2, 1))

local ref0 = "ctx2"
local ref1 = "Variable Ref"
local ref2 = "Variable Refs"
npc:say(GetText:trnc(ref0, ref1, ref2, 2))

--#. Comment 1
npc:say(GetText:trnc("ctx3", "Tr with args and comment", "Tr with args and comment plural", 1))

--#!ignore
npc:say(GetText:tr("IGNORED", 2))