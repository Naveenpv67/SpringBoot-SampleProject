FOR s IN _sets
  FILTER s.ns == @namespace
  RETURN s.name
