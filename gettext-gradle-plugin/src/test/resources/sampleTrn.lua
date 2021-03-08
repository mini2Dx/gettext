npc:say(GetText:trn("Simple", "Simples", 1))

npc:say(GetText:trn("Multi " .. "part", "Multi " .. "parts", 2, 1))

local ref1 = "Variable Ref"
local ref2 = "Variable Refs"
npc:say(GetText:trn(ref1, ref2, 2))

--#. Comment 1
npc:say(GetText:trn("Tr with args and comment", "Tr with args and comment plural", 1))

--#!ignore
npc:say(GetText:tr("IGNORED", 2))