npc:say(GetText:trc("ctx0", "Simple"))

npc:say(GetText:trc("ctx1", "Multi " .. "part"))

local ref1 = "Variable Ref"
npc:say(GetText:trc("ctx2", ref1))

--#. Comment 1
npc:say(GetText:trc("ctx3", "Tr with args and comment", 1))

--#!ignore
npc:say(GetText:tr("IGNORED", 2))