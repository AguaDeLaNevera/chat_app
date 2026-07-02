package com.example.chat.infrastructure.exceptions;

import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.stereotype.Controller;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;

@Controller
public class GraphQLExceptionHandler {

    @GraphQlExceptionHandler({
            UserAlreadyExistsException.class,
            UserNotFoundException.class,
            InvalidCredentialsException.class
    })
    public GraphQLError handle(RuntimeException ex) {
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .build();
    }
}