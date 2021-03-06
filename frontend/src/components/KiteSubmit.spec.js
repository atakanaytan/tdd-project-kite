import React from 'react';
import { render, fireEvent, waitForDomChange } from '@testing-library/react';
import KiteSubmit from './KiteSubmit';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import authReducer from '../redux/authReducer';
import * as apiCalls from '../api/apiCalls';

const defaultState = {
    id: 1,
    username: 'user1',
    displayName: 'display1',
    image: 'profile1.png',
    password: 'P4ssword',
    isLoggedIn: true
}

let store;

const setup = (state = defaultState) => {
    store = createStore(authReducer, state);
    return render(
        <Provider store={store}>
            <KiteSubmit />
        </Provider>
    );
};

describe('KiteSubmit', () => {
    describe('Layout', () => {
        it('has textarea', () => {
            const { container } = setup();
            const textarea = container.querySelector('textarea');
            expect(textarea).toBeInTheDocument();
        });
        it('has image', () => {
            const { container } = setup();
            const image = container.querySelector('img');
            expect(image).toBeInTheDocument();
        });
        it('displays textarea 1 line', () => {
            const { container } = setup();
            const textArea = container.querySelector('textarea');
            expect(textArea.rows).toBe(1);
        });
        it('displays user image', () => {
            const { container } = setup();
            const image = container.querySelector('img');
            expect(image.src).toContain('/images/profile/' + defaultState.image); 
        });
    }); 
    describe('Interactions', () => {
        it('displays 3 rows when focused to textarea', () => {
            const { container } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            expect(textArea.rows).toBe(3);
        });
        it('displays kite button when focused to textarea', () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            const kiteButton = queryByText('Kite');
            expect(kiteButton).toBeInTheDocument();
        });
        it('displays cancel button when focused to textarea', () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            const cancelButton = queryByText('Cancel');
            expect(cancelButton).toBeInTheDocument();
        });
        it('does not Cancel button when focused to textarea', () => {
            const { queryByText } = setup();
            const kiteButton = queryByText('Kite');
            expect(kiteButton).not.toBeInTheDocument();
        });
        it('does not display Cancel button when not focused to textarea', () => {
            const { queryByText } = setup();
            const cancelButton = queryByText('Cancel');
            expect(cancelButton).not.toBeInTheDocument();
        });
        it('returns back to unfocused state after clicking the cancel', () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            const cancelButton = queryByText('Cancel');
            fireEvent.click(cancelButton);
            expect(queryByText('Cancel')).not.toBeInTheDocument();
        });
        it('calls postKite with kite request object when clicking Kite', () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            fireEvent.change(textArea, { target: { value: 'Test kite content' } });

            const kiteButton = queryByText('Kite');

            apiCalls.postKite = jest.fn().mockResolvedValue( {} );
            fireEvent.click(kiteButton);

            expect(apiCalls.postKite).toHaveBeenCalledWith({
                content: 'Test kite content'
            });
        });
        it('returns back to unfocused state after successful postKite action', async () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            fireEvent.change(textArea, { target: { value: 'Test kite content' } });

            const kiteButton = queryByText('Kite');

            apiCalls.postKite = jest.fn().mockResolvedValue( {} );
            fireEvent.click(kiteButton);

            await waitForDomChange();

            expect(queryByText('Kite')).not.toBeInTheDocument();
        });
        it('clear content after successful postKite action', async () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            fireEvent.change(textArea, { target: { value: 'Test kite content' } });

            const kiteButton = queryByText('Kite');

            apiCalls.postKite = jest.fn().mockResolvedValue( {} );
            fireEvent.click(kiteButton);

            await waitForDomChange();

            expect(queryByText('Test kite content')).not.toBeInTheDocument();
        });
        it('clear content after clicking cancel', () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            fireEvent.change(textArea, { target: { value: 'Test kite content' } });
            
            fireEvent.click(queryByText('Cancel'));

            expect(queryByText('Test kite content')).not.toBeInTheDocument();
        });
        it('disables Kite button when there is postKite api call', () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            fireEvent.change(textArea, { target: { value: 'Test kite content' } });
            
            const kiteButton = queryByText('Kite');

            const mockFunction = jest.fn().mockImplementation(() => {
                return new Promise((resolve, reject) => {
                    setTimeout(() => {
                        resolve({})
                    }, 300)
                });
            });
            
            apiCalls.postKite = mockFunction;
            fireEvent.click(kiteButton);

            fireEvent.click(kiteButton);
            expect(mockFunction).toHaveBeenCalledTimes(1);
        });
        it('disables Cancel button when there is postKite api call', () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            fireEvent.change(textArea, { target: { value: 'Test kite content' } });
            
            const kiteButton = queryByText('Kite');

            const mockFunction = jest.fn().mockImplementation(() => {
                return new Promise((resolve, reject) => {
                    setTimeout(() => {
                        resolve({})
                    }, 300)
                });
            });
            
            apiCalls.postKite = mockFunction;
            fireEvent.click(kiteButton);

            const cancelButton = queryByText('Cancel');
            expect(cancelButton).toBeDisabled();
        });
        it('displays spinner when there is postKite api call', () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            fireEvent.change(textArea, { target: { value: 'Test kite content' } });
            
            const kiteButton = queryByText('Kite');

            const mockFunction = jest.fn().mockImplementation(() => {
                return new Promise((resolve, reject) => {
                    setTimeout(() => {
                        resolve({})
                    }, 300)
                });
            });
            
            apiCalls.postKite = mockFunction;
            fireEvent.click(kiteButton);

            expect(queryByText('Loading...')).toBeInTheDocument();
        });
        it('enables Kite button when kite api call fails', async () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            fireEvent.change(textArea, { target: { value: 'Test kite content' } });
            
            const kiteButton = queryByText('Kite');

            const mockFunction = jest.fn().mockRejectedValueOnce({
                response: {
                    data: {
                        validationErrors: {
                            content: 'It must have minimum 10 and maximum 5000 characters'
                        }
                    }
                }
            });
            
            apiCalls.postKite = mockFunction;
            fireEvent.click(kiteButton);

            await waitForDomChange();

            expect(queryByText('Kite')).not.toBeDisabled();
        });
        it('enables Cancel button when kite api call fails', async () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            fireEvent.change(textArea, { target: { value: 'Test kite content' } });
            
            const kiteButton = queryByText('Kite');

            const mockFunction = jest.fn().mockRejectedValueOnce({
                response: {
                    data: {
                        validationErrors: {
                            content: 'It must have minimum 10 and maximum 5000 characters'
                        }
                    }
                }
            });
            
            apiCalls.postKite = mockFunction;
            fireEvent.click(kiteButton);

            await waitForDomChange();

            expect(queryByText('Cancel')).not.toBeDisabled();
        });
        it('enables Kite button after successful postKite action', async () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            fireEvent.change(textArea, { target: { value: 'Test kite content' } });

            const kiteButton = queryByText('Kite');

            apiCalls.postKite = jest.fn().mockResolvedValue({});
            fireEvent.click(kiteButton);

            await waitForDomChange();
            fireEvent.focus(textArea);
            expect(queryByText('Kite')).not.toBeDisabled();
        });
        it('displays validation error for content', async () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            fireEvent.change(textArea, { target: { value: 'Test kite content' } });
            
            const kiteButton = queryByText('Kite');

            const mockFunction = jest.fn().mockRejectedValueOnce({
                response: {
                    data: {
                        validationErrors: {
                            content: 'It must have minimum 10 and maximum 5000 characters'
                        }
                    }
                }
            });
            
            apiCalls.postKite = mockFunction;
            fireEvent.click(kiteButton);

            await waitForDomChange();

            expect(
                queryByText('It must have minimum 10 and maximum 5000 characters')
            ).toBeInTheDocument();
        });
        it('clears validation error after clicking cancel', async () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            fireEvent.change(textArea, { target: { value: 'Test kite content' } });
            
            const kiteButton = queryByText('Kite');

            const mockFunction = jest.fn().mockRejectedValueOnce({
                response: {
                    data: {
                        validationErrors: {
                            content: 'It must have minimum 10 and maximum 5000 characters'
                        }
                    }
                }
            });
            
            apiCalls.postKite = mockFunction;
            fireEvent.click(kiteButton);

            await waitForDomChange();
            fireEvent.click(queryByText('Cancel'));

            expect(
                queryByText('It must have minimum 10 and maximum 5000 characters')
            ).not.toBeInTheDocument();
        });
        it('clears validation error after content is changed', async () => {
            const { container, queryByText } = setup();
            const textArea = container.querySelector('textarea');
            fireEvent.focus(textArea);
            fireEvent.change(textArea, { target: { value: 'Test kite content' } });
            
            const kiteButton = queryByText('Kite');

            const mockFunction = jest.fn().mockRejectedValueOnce({
                response: {
                    data: {
                        validationErrors: {
                            content: 'It must have minimum 10 and maximum 5000 characters'
                        }
                    }
                }
            });
            
            apiCalls.postKite = mockFunction;
            fireEvent.click(kiteButton);

            await waitForDomChange();
            fireEvent.change(textArea, {
                target: { value: 'Test kite content updated' }
            });

            expect(
                queryByText('It must have minimum 10 and maximum 5000 characters')
            ).not.toBeInTheDocument();
        });
    });
});

console.error = () => {};