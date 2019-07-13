npc:say(GetText:tr("Simple"))

npc:say(GetText:tr("Multi " .. "part"))

local ref1 = "Variable Ref"
npc:say(GetText:tr(ref1))

-- #. Comment 1
npc:say(GetText:tr("Tr with args and comment", 1))
