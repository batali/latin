package latin.forms;

import latin.util.PathId;

import java.util.function.Function;

public interface Rule extends Function<Form,Form>, PathId.Identified {
}