npc:say(GetText:tr("Simple"))

npc:say(GetText:tr("Multi " .. "part"))

local ref1 = "Variable Ref"
npc:say(GetText:tr(ref1))

--#. Comment 1
npc:say(GetText:tr("Tr with args and comment", 1))

--#!extract
local tbl = {"value1", "value2"}
--#!extract
--#. Comment 2
table.insert(tbl, "value0")

--#!ignore
npc:say(GetText:tr("IGNORED", 2))